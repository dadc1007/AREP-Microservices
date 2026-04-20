create table streams (
    id bigserial primary key,
    type varchar(50) not null unique,
    name varchar(255) not null
);

create table users (
    id varchar(36) primary key,
    auth0_id varchar(255) not null unique,
    username varchar(255) unique,
    email varchar(255) not null unique
);

create table posts (
    id bigserial primary key,
    content varchar(140) not null,
    user_id varchar(36) not null,
    stream_id bigint not null,
    constraint fk_posts_user foreign key (user_id) references users (id),
    constraint fk_posts_stream foreign key (stream_id) references streams (id)
);
