create extension if not exists "uuid-ossp";

CREATE TABLE IF NOT EXISTS "museum" (
    id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v1() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    photo TEXT NOT NULL
);

insert into "museum" (id, title, description, photo) values
(uuid_generate_v1(), 'Третьяковка', '', '');
(uuid_generate_v1(), '', '', '');