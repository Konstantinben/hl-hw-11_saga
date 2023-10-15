create table if not exists warehouse_item (
    item_id serial primary key,
    item_uuid uuid not null unique default uuid_generate_v4(),
    item_name text not null,
    item_quantity smallint,
    price integer
);


create table if not exists order_user (
    order_id serial primary key,
    order_uuid uuid not null default uuid_generate_v4(),
    user_uuid uuid not null,
    etag integer not null,
    total_price integer,
    confirmed boolean
);

create table if not exists order_item (
    order_id integer, --references order_user (order_id) -- disabled due to spring jdbc limitations
    item_id integer references warehouse_item(item_id),
    item_uuid uuid not null references warehouse_item(item_uuid),
    quantity smallint,
    price integer,
    primary key (order_id, item_id)
);

