-- 기술 스택 (skill)
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'REACT') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'FLUTTER') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'IOS') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'ANDROID') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'NODEJS') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'PYTHON') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'AI') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'SPRING') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'JAVA') ON CONFLICT (name) DO NOTHING;
INSERT INTO skill (skill_id, name) VALUES (nextval('skill_seq'), 'UNITY') ON CONFLICT (name) DO NOTHING;

-- 포지션 (occupation)
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'FE') ON CONFLICT (name) DO NOTHING;
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'BE') ON CONFLICT (name) DO NOTHING;
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'AI') ON CONFLICT (name) DO NOTHING;
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'DESIGN') ON CONFLICT (name) DO NOTHING;
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'PM') ON CONFLICT (name) DO NOTHING;
INSERT INTO occupation (occupation_id, name) VALUES (nextval('occupation_seq'),'ETC') ON CONFLICT (name) DO NOTHING;