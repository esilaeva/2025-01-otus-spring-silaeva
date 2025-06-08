document.addEventListener('DOMContentLoaded', function() {
    loadAuthorsAndGenres();
    setupFormSubmit();
});

async function loadAuthorsAndGenres() {
    try {
        const [authorsResponse, genresResponse] = await Promise.all([
            fetch('/api/v2/author'),
            fetch('/api/v2/genre')
        ]);

        if (!authorsResponse.ok || !genresResponse.ok) {
            throw new Error('Failed to load required data');
        }

        const authors = await authorsResponse.json();
        const genres = await genresResponse.json();

        populateDropdown('author-input', authors, 'fullName');
        populateDropdown('genre-input', genres, 'name');
    } catch (error) {
        showError('Failed to load required data. Please try again later.');
        console.error('Error:', error);
    }
}

function populateDropdown(dropdownId, items, textProperty) {
    const dropdown = document.getElementById(dropdownId);
    dropdown.innerHTML = '<option value="0">select ' +
        dropdownId.replace('-input', '') + '</option>';

    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.textContent = item[textProperty];
        dropdown.appendChild(option);
    });
}

function setupFormSubmit() {
    const form = document.getElementById('create-book-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearErrors();

        const formData = {
            title: document.getElementById('title-input').value.trim(),
            authorId: document.getElementById('author-input').value,
            genreId: document.getElementById('genre-input').value
        };

        // Validation
        if (!formData.title) {
            showError('Please enter a book title');
            return;
        }

        if (formData.authorId === '0' || formData.genreId === '0') {
            showError('Please select both author and genre');
            return;
        }

        try {
            const response = await fetch('/api/v2/book', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create book');
            }

            window.location.href = 'books.html';
        } catch (error) {
            showError(error.message);
            console.error('Error:', error);
        }
    });
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function clearErrors() {
    document.getElementById('error-message').style.display = 'none';
}