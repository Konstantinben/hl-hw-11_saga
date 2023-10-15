CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists auth_user (
	id serial primary key,
	email text not null unique,
	uuid uuid not null default uuid_generate_v4(),
    role varchar(20),
    password varchar(255)
);

