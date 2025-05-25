document.addEventListener('DOMContentLoaded', function() {
    const bookId = getBookIdFromURL();
    if (!bookId) {
        showError('Invalid book ID');
        return;
    }

    initializePage(bookId);
});

function getBookIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

async function initializePage(bookId) {
    try {
        const [book, authors, genres] = await Promise.all([
            fetchBook(bookId),
            fetch('/api/v1/author').then(handleResponse),
            fetch('/api/v1/genre').then(handleResponse)
        ]);

        populateForm(book);
        populateDropdown('author-input', authors, 'fullName', book.author.id);
        populateDropdown('genre-input', genres, 'name', book.genre.id);
        setupFormSubmit();
    } catch (error) {
        showError(error.message);
    }
}

async function fetchBook(bookId) {
    const response = await fetch(`/api/v1/book/${bookId}`);
    return handleResponse(response);
}

function handleResponse(response) {
    if (!response.ok) {
        return response.json().then(error => {
            throw new Error(error.message || 'Request failed');
        });
    }
    return response.json();
}

function populateForm(book) {
    document.getElementById('id-input').value = book.id;
    document.getElementById('title-input').value = book.title;
}

function populateDropdown(dropdownId, items, textProperty, selectedId) {
    const dropdown = document.getElementById(dropdownId);
    dropdown.innerHTML = '';

    const defaultOption = document.createElement('option');
    defaultOption.value = '0';
    defaultOption.textContent = `Select ${dropdownId.replace('-input', '')}`;
    dropdown.appendChild(defaultOption);

    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.textContent = item[textProperty];
        option.selected = item.id === selectedId;
        dropdown.appendChild(option);
    });
}

function setupFormSubmit() {
    const form = document.getElementById('edit-book-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearErrors();

        const formData = {
            id: document.getElementById('id-input').value,
            title: document.getElementById('title-input').value.trim(),
            authorId: document.getElementById('author-input').value,
            genreId: document.getElementById('genre-input').value
        };

        if (!validateForm(formData)) return;

        try {
            const response = await fetch('/api/v1/book', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Update failed');
            }

            window.location.href = 'books.html';
        } catch (error) {
            showError(error.message);
        }
    });
}

function validateForm(formData) {
    if (!formData.title) {
        showError('Please enter a book title');
        return false;
    }

    if (formData.authorId === '0' || formData.genreId === '0') {
        showError('Please select both author and genre');
        return false;
    }

    return true;
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function clearErrors() {
    document.getElementById('error-message').style.display = 'none';
}