create table authors
(
    id        bigserial,
    full_name varchar(255),
    primary key (id)
);

create table genres
(
    id   bigserial,
    name varchar(255),
    primary key (id)
);

create table books
(
    id        bigserial,
    title     varchar(255),
    author_id bigint references authors (id) on delete cascade,
    genre_id  bigint references genres (id) on delete cascade,
    primary key (id)
);

create table comments
(
    id              bigserial,
    comment_content varchar(1000),
    book_id         bigint references books (id) on delete cascade,
    primary key (id)
);

create table users
(
    id       bigserial,
    username varchar(255) not null unique,
    password varchar(255) not null,
    role     varchar(255) not null,
    primary key (id)
);

-- Create acl_sid table
CREATE TABLE acl_sid (
    id BIGINT NOT NULL PRIMARY KEY,
    principal TINYINT NOT NULL,
    sid VARCHAR(100) NOT NULL
);

-- Add unique constraint for acl_sid
ALTER TABLE acl_sid ADD CONSTRAINT unique_uk_1 UNIQUE (sid, principal);

-- Create acl_class table
CREATE TABLE acl_class (
    id BIGINT NOT NULL PRIMARY KEY,
    class VARCHAR(255) NOT NULL
);

-- Add unique constraint for acl_class
ALTER TABLE acl_class ADD CONSTRAINT unique_uk_2 UNIQUE (class);

-- Create acl_object_identity table
CREATE TABLE acl_object_identity (
    id BIGINT NOT NULL PRIMARY KEY,
    object_id_class BIGINT NOT NULL,
    object_id_identity BIGINT NOT NULL,
    parent_object BIGINT,
    owner_sid BIGINT,
    entries_inheriting TINYINT NOT NULL
);

-- Add unique constraint for acl_object_identity
ALTER TABLE acl_object_identity ADD CONSTRAINT unique_uk_3 UNIQUE (object_id_class, object_id_identity);

-- Create acl_entry table
CREATE TABLE acl_entry (
    id BIGINT NOT NULL PRIMARY KEY,
    acl_object_identity BIGINT NOT NULL,
    ace_order INT NOT NULL,
    sid BIGINT NOT NULL,
    mask INT NOT NULL,
    granting TINYINT NOT NULL,
    audit_success TINYINT NOT NULL,
    audit_failure TINYINT NOT NULL
);

-- Add unique constraint for acl_entry
ALTER TABLE acl_entry ADD CONSTRAINT unique_uk_4 UNIQUE (acl_object_identity, ace_order);

-- Add foreign keys for acl_object_identity
ALTER TABLE acl_object_identity ADD CONSTRAINT fk_object_class
    FOREIGN KEY (object_id_class) REFERENCES acl_class(id);
ALTER TABLE acl_object_identity ADD CONSTRAINT fk_owner_sid
    FOREIGN KEY (owner_sid) REFERENCES acl_sid(id);
ALTER TABLE acl_object_identity ADD CONSTRAINT fk_id_parent
    FOREIGN KEY (parent_object) REFERENCES acl_object_identity(id);

-- Add foreign keys for acl_entry
ALTER TABLE acl_entry ADD CONSTRAINT fk_obj_identity
    FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity(id);
ALTER TABLE acl_entry ADD CONSTRAINT fk_acl_sid
    FOREIGN KEY (sid) REFERENCES acl_sid(id);