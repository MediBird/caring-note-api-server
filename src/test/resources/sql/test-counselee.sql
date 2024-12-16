INSERT INTO counselees (
    id,
    counsel_count,
    date_of_birth,
    last_counsel_date,
    name,
    notes,
    phone_number,
    registration_date,
    created_date,
    updated_date,
    is_disability
) VALUES (
             'TEST-COUNSELEE-01',
             5,
             '1990-05-15',
             '2024-12-01',
             'John Doe',
             NULL, -- No notes
             '01012345678',
             '2024-01-01',
             '2024-01-01',
             '2024-12-01',
          true
         );

INSERT INTO counselees (
    id,
    counsel_count,
    date_of_birth,
    last_counsel_date,
    name,
    notes,
    phone_number,
    registration_date,
    created_date,
    updated_date,
    is_disability
) VALUES (
             'TEST-COUNSELEE-02',
             0,
             '1985-10-10',
             NULL, -- No last counseling date
             'Jane Smith',
             NULL, -- No notes
             '01087654321',
             '2024-01-10',
             '2024-01-10',
             '2024-12-01'
    ,false
         );