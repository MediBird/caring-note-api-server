INSERT INTO medication_counsels (
    id,
    counsel_session_id,
    counsel_record,
    counsel_record_highlights,
    counsel_need_status
)
VALUES (
        'TEST-MEDICATION-COUNSEL-00',
           'TEST-COUNSEL-SESSION-00', -- counsel_session_id
           '안녕 안녕 안녕', -- counsel_record
           '안녕^=^안녕', -- counsel_record_highlights (TEXT)
           'ONE' -- counsel_need_status (Enum)
       );

INSERT INTO medication_counsels (
    id,
    counsel_session_id,
    counsel_record,
    counsel_record_highlights,
    counsel_need_status
)
VALUES (
           'TEST-MEDICATION-COUNSEL-01',
           'TEST-COUNSEL-SESSION-01', -- counsel_session_id
           '안녕 안녕 안녕 안녕', -- counsel_record
           '안녕^=^안녕', -- counsel_record_highlights (TEXT)
           'ONE' -- counsel_need_status (Enum)
       );