document.addEventListener('DOMContentLoaded', function() {
    loadBooks();
});

async function loadBooks() {
    try {
        const response = await fetch('/api/v2/book');
        if (!response.ok) {
            const error = await response.json();
            throw new Error(`${error.code} - ${error.message}`);
        }
        const books = await response.json();
        populateBookTable(books);
    } catch (error) {
        alert(`Error loading books: ${error.message}`);
        console.error('Error:', error);
    }
}

function populateBookTable(books) {
    const tbody = document.getElementById('book-list');
    tbody.innerHTML = ''; // Clear existing content

    books.forEach(book => {
        const row = document.createElement('tr');

        // ID Column
        const idCell = document.createElement('td');
        idCell.textContent = book.id;
        row.appendChild(idCell);

        // Title Column
        const titleCell = document.createElement('td');
        titleCell.textContent = book.title;
        row.appendChild(titleCell);

        // Author Column
        const authorCell = document.createElement('td');
        authorCell.textContent = book.author.fullName;
        row.appendChild(authorCell);

        // Genre Column
        const genreCell = document.createElement('td');
        genreCell.textContent = book.genre.name;
        row.appendChild(genreCell);

        // Actions Column
        const actionsCell = document.createElement('td');
        actionsCell.appendChild(createActionButtons(book.id));
        row.appendChild(actionsCell);

        tbody.appendChild(row);
    });
}

function createActionButtons(bookId) {
    const container = document.createElement('div');

    // Comments Button
    const commentsBtn = document.createElement('a');
    commentsBtn.className = 'btn btn-link';
    commentsBtn.href = `comments.html?bookId=${bookId}`;
    commentsBtn.textContent = 'Comments';
    container.appendChild(commentsBtn);

    // Edit Button
    const editBtn = document.createElement('a');
    editBtn.className = 'btn btn-link';
    editBtn.href = `book_edit.html?id=${bookId}`;
    editBtn.textContent = 'Edit';
    container.appendChild(editBtn);

    // Delete Button
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-link';
    deleteBtn.textContent = 'Remove';
    deleteBtn.addEventListener('click', () => deleteBook(bookId));
    container.appendChild(deleteBtn);

    return container;
}

async function deleteBook(bookId) {
    if (!confirm('Are you sure you want to delete this book?')) return;

    try {
        const response = await fetch(`/api/v2/book/${bookId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`${error.code} - ${error.message}`);
        }

        loadBooks(); // Refresh the book list
    } catch (error) {
        alert(`Error deleting book: ${error.message}`);
        console.error('Error:', error);
    }
}