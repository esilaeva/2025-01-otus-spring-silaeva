create table authors
(
    id        bigint auto_increment,
    full_name varchar(255),
    primary key (id)
);

create table genres
(
    id   bigint auto_increment,
    name varchar(255),
    primary key (id)
);

create table books
(
    id        bigint auto_increment,
    title     varchar(255),
    author_id bigint references authors (id) on delete cascade,
    genre_id  bigint references genres (id) on delete cascade,
    primary key (id)
);

create table comments
(
    id              bigint auto_increment,
    comment_content varchar(1000),
    book_id         bigint references books (id) on delete cascade,
    primary key (id)
);