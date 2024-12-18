INSERT INTO counsel_sessions (
    id,
    scheduled_start_datetime,
    status,
    counselee_id,
    counselor_id,
    created_datetime,
    updated_datetime,
    end_datetime,
    start_datetime
) VALUES (
             'TEST-COUNSEL-SESSION-00',
             '2024-12-14 10:00:00',
             'COMPLETED',
             'TEST-COUNSELEE-01', -- Assume CNL001 exists in counselees table
             'TEST-COUNSELOR-02', -- Assume CNS001 exists in counselors table
             '2024-12-10 10:00:00',
             '2024-12-12 10:00:00',
             '2024-12-14 10:00:00',
             '2024-12-14 12:00:00'
         );
INSERT INTO counsel_sessions (
    id,
    scheduled_start_datetime,
    status,
    counselee_id,
    counselor_id,
    created_datetime,
    updated_datetime,
    end_datetime,
    start_datetime
) VALUES (
             'TEST-COUNSEL-SESSION-01',
             '2024-12-15 10:00:00',
             'SCHEDULED',
             'TEST-COUNSELEE-01', -- Assume CNL001 exists in counselees table
             'TEST-COUNSELOR-02', -- Assume CNS001 exists in counselors table
             '2024-12-10 10:00:00',
             '2024-12-12 10:00:00',
             NULL, -- No end datetime yet
             NULL  -- No start datetime yet
         );

INSERT INTO counsel_sessions (
    id,
    scheduled_start_datetime,
    status,
    counselee_id,
    counselor_id,
    created_datetime,
    updated_datetime,
    end_datetime,
    start_datetime
) VALUES (
             'TEST-COUNSEL-SESSION-02',
             '2024-12-15 14:00:00',
             'SCHEDULED',
             'TEST-COUNSELEE-01', -- Assume CNL001 exists in counselees table
             'TEST-COUNSELOR-01', -- Assume CNS001 exists in counselors table
             '2024-12-10 10:00:00',
             '2024-12-12 10:00:00',
             NULL, -- No end datetime yet
             NULL  -- No start datetime yet
         );


INSERT INTO counsel_sessions (
    id,
    scheduled_start_datetime,
    status,
    counselee_id,
    counselor_id,
    created_datetime,
    updated_datetime,
    end_datetime,
    start_datetime
) VALUES (
             'TEST-COUNSEL-SESSION-03',
             '2024-12-16 14:00:00',
             'SCHEDULED',
             'TEST-COUNSELEE-01', -- Assume CNL001 exists in counselees table
             'TEST-COUNSELOR-01', -- Assume CNS001 exists in counselors table
             '2024-12-10 10:00:00',
             '2024-12-12 10:00:00',
             NULL, -- No end datetime yet
             NULL  -- No start datetime yet
         );