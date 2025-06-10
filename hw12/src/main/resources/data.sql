insert into authors(full_name)
values ('Author_1'),
       ('Author_2'),
       ('Author_3');

insert into genres(name)
values ('Genre_1'),
       ('Genre_2'),
       ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1),
       ('BookTitle_2', 2, 2),
       ('BookTitle_3', 3, 3);

insert into comments(comment_content, book_id)
values ('Comment_1_for_BookTitle_1', 1),
       ('Comment_2_for_BookTitle_1', 1),
       ('Comment_3_for_BookTitle_1', 1),
       ('Comment_1_for_BookTitle_2', 2),
       ('Comment_2_for_BookTitle_2', 2),
       ('Comment_1_for_BookTitle_3', 3);

insert into users (username, password)
values ('user', '$argon2id$v=19$m=16,t=2,p=1$UE5udDNzUUZiQTJGbDM4ag$kppueO80r4g6o/i8JeL47GIJejpgGfkTvoVCpROCPog');