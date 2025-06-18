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

insert into users (username, password, role)
values ('john', '$argon2id$v=19$m=16,t=2,p=1$UE5udDNzUUZiQTJGbDM4ag$kppueO80r4g6o/i8JeL47GIJejpgGfkTvoVCpROCPog', 'USER'),
       ('michel', '$argon2id$v=19$m=16,t=2,p=1$S0tkUFhGSmRrQzY3Q2s4SQ$8AIc1zC8E1+u6JgpCMfdGA', 'USER');


INSERT INTO acl_sid (id, principal, sid) VALUES (1, 1, 'john');
INSERT INTO acl_sid (id, principal, sid) VALUES (2, 1, 'michel');
INSERT INTO acl_sid (id, principal, sid) VALUES (3, 0, 'ROLE_USER');


INSERT INTO acl_class (id, class) VALUES (1, 'ru.otus.hw.dto.BookDto');

-- object_id_class -> ru.otus.hw.dto.BookDto; object_id_identity -> BookDto index
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (1, 1, 1, 0, 3);
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (2, 1, 2, 0, 3);
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (3, 1, 3, 0, 3);

-- BookDto # 1, john, Read, Allow, AuditSuccess, AuditFailure. For John all 3 books are allowed
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (2, 2, 1, 1, 1, 1, 1, 1);
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (3, 3, 1, 1, 1, 1, 1, 1);

-- BookDto # 1, michel, Read, Deny, AuditSuccess, AuditFailure. For Michel Book #1 are NOT allowed
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (4, 1, 2, 2, 1, 0, 1, 1);
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (5, 2, 2, 2, 1, 1, 1, 1);
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (6, 3, 2, 2, 1, 1, 1, 1);
