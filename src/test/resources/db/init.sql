-- Insert users
INSERT INTO users ( username, password, enabled )
VALUES ( 'sou', '$2a$10$DNCqQ8dNy9fETdUspssNF.G2ws4X8MG3WVL13WwyvgldoVeZeOsmi', true )
ON CONFLICT DO NOTHING;

-- Insert roles
INSERT INTO user_roles ( user_id, role )
SELECT id, 'admin'
FROM users
WHERE username = 'sou'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles ( user_id, role )
SELECT id, 'user'
FROM users
WHERE username = 'sou'
ON CONFLICT DO NOTHING;
