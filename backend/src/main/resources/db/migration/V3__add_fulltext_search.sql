-- Migration V3: Add full-text search index for members

CREATE INDEX idx_members_search ON members 
USING gin(to_tsvector('simple', full_name || ' ' || COALESCE(branch_name, '') || ' ' || COALESCE(notes, '')));
