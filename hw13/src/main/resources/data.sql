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


-- Insert ACL SIDs
INSERT INTO acl_sid (principal, sid) VALUES (1, 'john');
INSERT INTO acl_sid (principal, sid) VALUES (1, 'michel');
INSERT INTO acl_sid (principal, sid) VALUES (0, 'ROLE_USER');

-- Insert ACL Class
INSERT INTO acl_class (class) VALUES ('ru.otus.hw.models.Book');

-- Insert ACL Object Identities (use subqueries for owner_sid/class)
INSERT INTO acl_object_identity (object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (
  (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book'),
  1,
  0,
  (SELECT id FROM acl_sid WHERE sid = 'ROLE_USER')
);

INSERT INTO acl_object_identity (object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (
  (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book'),
  2,
  0,
  (SELECT id FROM acl_sid WHERE sid = 'ROLE_USER')
);

INSERT INTO acl_object_identity (object_id_class, object_id_identity, entries_inheriting, owner_sid)
VALUES (
  (SELECT id FROM acl_class WHERE class = 'ru.otus.hw.models.Book'),
  3,
  0,
  (SELECT id FROM acl_sid WHERE sid = 'ROLE_USER')
);

-- Insert ACL Entries (resolve all foreign keys via subqueries)
-- Book 1 entries
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 1),
  1,
  (SELECT id FROM acl_sid WHERE sid = 'john'),
  1, 1, 1, 1
);

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 1),
  2,
  (SELECT id FROM acl_sid WHERE sid = 'michel'),
  1, 0, 1, 1
);

-- Book 2 entries
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 2),
  1,
  (SELECT id FROM acl_sid WHERE sid = 'john'),
  1, 1, 1, 1
);

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 2),
  2,
  (SELECT id FROM acl_sid WHERE sid = 'michel'),
  1, 1, 1, 1
);

-- Book 3 entries
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 3),
  1,
  (SELECT id FROM acl_sid WHERE sid = 'john'),
  1, 1, 1, 1
);

INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (
  (SELECT id FROM acl_object_identity WHERE object_id_identity = 3),
  2,
  (SELECT id FROM acl_sid WHERE sid = 'michel'),
  1, 1, 1, 1
);