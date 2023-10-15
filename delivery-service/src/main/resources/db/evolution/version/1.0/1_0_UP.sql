create table if not exists delivery (
    user_uuid uuid not null,
    order_uuid uuid not null,
    delivery_date date not null,
    start_time time not null,
    end_time time not null,
    confirmed boolean
);

