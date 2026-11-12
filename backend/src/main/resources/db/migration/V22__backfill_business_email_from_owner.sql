UPDATE businesses b
SET email = (
    SELECT u.email
    FROM users u
    WHERE u.business_id = b.id
      AND u.role = 'BUSINESS_OWNER'
    ORDER BY u.id
    LIMIT 1
)
WHERE b.email IS NULL
  AND EXISTS (
      SELECT 1
      FROM users u
      WHERE u.business_id = b.id
        AND u.role = 'BUSINESS_OWNER'
  );
