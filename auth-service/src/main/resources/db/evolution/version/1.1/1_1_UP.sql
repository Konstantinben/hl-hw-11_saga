insert into auth_user (uuid, email, role, password)
values ('50109079-8dec-4847-868e-717c77deb881', 'admin@mshw.com', 'ADMIN', '$2a$12$f4S1O26MA8q9LcJkSJyZKuHA0IldecXALqfSG3L/cjwm4QEv5ki8C')
returning id, uuid;

