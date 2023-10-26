create table payments(
    payment_id uuid PRIMARY KEY,
    order_number VARCHAR,
    value bigint,
    cpf VARCHAR,
    name_card VARCHAR,
    number VARCHAR,
    due_date VARCHAR,
    code VARCHAR,
    payment_status VARCHAR,
    user_id uuid,
    name_user VARCHAR,
    email VARCHAR
);