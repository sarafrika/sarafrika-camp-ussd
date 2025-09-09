-- Camp Sarafrika: Add sample YMAC camps as per document specification
INSERT INTO camps (name, category, location, dates, fee, camp_type) VALUES
('YMAC Consolata School', 'Young Musicians & Artists Camp (YMAC)', 'Kiambu', '17th-29th Nov', 12500.00, 'HALF_DAY'),
('YMAC Creative Intergrated School', 'Young Musicians & Artists Camp (YMAC)', 'Nairobi', '3rd-20th Nov', 12500.00, 'HALF_DAY'),
('YMAC - Olerai Rongai School', 'Young Musicians & Artists Camp (YMAC)', 'Rongai', 'Dec 1st-12th', 12500.00, 'HALF_DAY'),
('YMAC - Olerai Kiserian School', 'Young Musicians & Artists Camp (YMAC)', 'Rongai', 'Dec 1st-12th', 12500.00, 'HALF_DAY'),
('YMAC - Garden Brook School', 'Young Musicians & Artists Camp (YMAC)', 'Rongai', 'Dec 1st-12th', 12500.00, 'HALF_DAY'),
('YMAC - Chantilly School', 'Young Musicians & Artists Camp (YMAC)', 'Rongai', 'Dec 1st-12th', 12500.00, 'HALF_DAY'),
('YMAC - White Cottage School', 'Young Musicians & Artists Camp (YMAC)', 'Rongai', 'Dec 1st-12th', 12500.00, 'HALF_DAY');

-- Add other camp categories from the document
INSERT INTO camps (name, category, location, dates, fee, camp_type) VALUES
('Sports Excellence Academy', 'Sports', 'Nairobi Sports Club', 'Dec 5-15', 15000.00, 'HALF_DAY'),
('Science & Tech Innovation Hub', 'Science & Tech', 'University of Nairobi', 'Jan 8-18', 18000.00, 'HALF_DAY'),
('Cultural Heritage Experience', 'Culture & Heritage', 'National Museums', 'Dec 12-22', 13000.00, 'HALF_DAY'),
('Outdoor Adventure Camp', 'Outdoor & Adventure', 'Aberdare National Park', 'Dec 20-30', 25000.00, 'BOOT_CAMP'),
('Tech Coding Bootcamp', 'Science & Tech', 'iHub Nairobi', 'Jan 15-25', 22000.00, 'BOOT_CAMP');