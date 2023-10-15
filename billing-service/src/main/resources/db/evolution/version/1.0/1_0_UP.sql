create table if not exists balance (
    user_uuid uuid not null primary key,
    balance integer not null
);

create table if not exists orders (
    order_uuid uuid not null,
    user_uuid uuid not null references balance(user_uuid),
    total_price integer not null,
    paid boolean default false,
    primary key (order_uuid, user_uuid)
);

