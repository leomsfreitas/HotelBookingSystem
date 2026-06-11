-- =============================================================
-- Schema
-- =============================================================

CREATE TABLE IF NOT EXISTS app_user (
    id       varchar(36)  NOT NULL,
    name     varchar(255) NOT NULL,
    lastname varchar(255) NOT NULL,
    email    varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    role     varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS guest (
    id   varchar(36)  NOT NULL,
    name varchar(255) NOT NULL,
    cpf  varchar(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (cpf)
);

CREATE TABLE IF NOT EXISTS booking (
    id            varchar(36)   NOT NULL,
    guest_id      varchar(36)   NOT NULL,
    room_category varchar(255)  NOT NULL,
    check_in      date          NOT NULL,
    check_out     date          NOT NULL,
    total_value   numeric(38,2) NOT NULL,
    status        varchar(255)  NOT NULL,
    PRIMARY KEY (id)
);

-- =============================================================
-- Dados mockados
-- =============================================================

-- Usuário padrão (senha: admin123)
INSERT INTO app_user (id, name, lastname, email, password, role)
VALUES (
    'f15d2fde-8c2a-4aee-9d35-cdf2884d169d',
    'Admin', 'Hotel', 'admin@hotel.com',
    '$2a$10$PrAg4BXckcMA9yRFQyqUhOgqt0IAj4GdKNMbmoPUjTbpl8D.9WbJu',
    'USER'
);

-- Hóspedes
INSERT INTO guest (id, name, cpf) VALUES ('d203ae32-5f90-4549-b53c-5b55764d05b5', 'Carlos Silva',    '123.456.789-09');
INSERT INTO guest (id, name, cpf) VALUES ('e078b6ba-0582-4743-b143-8a8accc107c0', 'Ana Souza',       '987.654.321-00');
INSERT INTO guest (id, name, cpf) VALUES ('33ec0805-8842-4932-ad3c-2b9bf4ab34e8', 'Pedro Lima',      '111.444.777-35');
INSERT INTO guest (id, name, cpf) VALUES ('34c410df-cca4-4d95-bbb1-3ec7f140f7a5', 'Mariana Costa',   '222.333.444-05');
INSERT INTO guest (id, name, cpf) VALUES ('74ce7ba2-a551-4063-8f48-37b46e4b65c8', 'Roberto Alves',   '555.666.777-20');
INSERT INTO guest (id, name, cpf) VALUES ('b77bd823-696b-44e0-b338-88a70da39e52', 'Fernanda Rocha',  '888.999.000-78');
INSERT INTO guest (id, name, cpf) VALUES ('3f3ccb70-4d65-4cc8-abd4-e64c348fcb5a', 'Luciano Martins', '135.792.468-28');

-- Reservas
-- PENDING
INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('1b0e6dcf-a025-4a7d-aaf4-f0e8ec929b0c', 'd203ae32-5f90-4549-b53c-5b55764d05b5', 'STANDARD',
        '2026-05-15', '2026-05-18',  450.00, 'PENDING');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('49cf0447-120c-4c30-8d28-f94d0e3a1899', 'e078b6ba-0582-4743-b143-8a8accc107c0', 'DELUXE',
        '2026-05-20', '2026-05-25', 1250.00, 'PENDING');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('20168b14-fde1-4aa4-bec4-bebc0d9009ca', '33ec0805-8842-4932-ad3c-2b9bf4ab34e8', 'SUITE',
        '2026-06-01', '2026-06-05', 1600.00, 'PENDING');

-- CHECKED_IN
INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('a48e82b8-b6c3-4a48-9367-0f78daeed099', '34c410df-cca4-4d95-bbb1-3ec7f140f7a5', 'STANDARD',
        '2026-05-06', '2026-05-10',  600.00, 'CHECKED_IN');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('b64c331b-6d45-47d6-9e04-bb15b84cd290', '74ce7ba2-a551-4063-8f48-37b46e4b65c8', 'SUITE',
        '2026-05-07', '2026-05-12', 2000.00, 'CHECKED_IN');

-- COMPLETED
INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('d684a6fb-7106-42c0-a474-d28fccff872c', 'b77bd823-696b-44e0-b338-88a70da39e52', 'DELUXE',
        '2026-04-20', '2026-04-25', 1000.00, 'COMPLETED');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('a834cd63-d2c7-4963-9081-2cf5311fd23e', 'd203ae32-5f90-4549-b53c-5b55764d05b5', 'STANDARD',
        '2026-04-10', '2026-04-13',  450.00, 'COMPLETED');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('635b2274-5bf9-4d78-9dd2-900cc8faafc7', 'e078b6ba-0582-4743-b143-8a8accc107c0', 'SUITE',
        '2026-03-15', '2026-03-20', 2000.00, 'COMPLETED');

-- CANCELLED
INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('60fc8c9d-eded-4e57-9eb6-4db517c506fb', '3f3ccb70-4d65-4cc8-abd4-e64c348fcb5a', 'STANDARD',
        '2026-05-01', '2026-05-05',  450.00, 'CANCELLED');

INSERT INTO booking (id, guest_id, room_category, check_in, check_out, total_value, status)
VALUES ('4f9dcaf2-679d-438d-818f-0ff5b1a7a2e2', '33ec0805-8842-4932-ad3c-2b9bf4ab34e8', 'DELUXE',
        '2026-04-28', '2026-05-03', 1000.00, 'CANCELLED');
