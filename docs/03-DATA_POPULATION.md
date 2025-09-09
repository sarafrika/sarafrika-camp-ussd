# Data Population Guide

## Overview

This guide provides comprehensive instructions for populating the Camp Sarafrika database using API endpoints to achieve the desired USSD flow. All data is managed through REST APIs rather than direct database manipulation, ensuring proper validation, relationships, and audit trails.

## API-Based Data Population Strategy

### 1. Foundation Data (Locations)
First, establish locations with their associated fees through the Location API, as these form the pricing foundation.

### 2. Camp Categories
Create camps organized by the five main categories using the Camp API.

### 3. Activities
Populate activities for each camp using the Activity API with proper camp relationships.

### 4. Relationship Management
Camp-location relationships are managed automatically through the normalized database structure.

## Step-by-Step API Population

### Step 1: Populate Locations via API

Use the Location Management API to create locations with standardized pricing:

**Endpoint**: `POST /api/locations`

**Primary locations from PDF specification:**
```json
POST /api/locations
Content-Type: application/json

{
  "name": "Kiambu",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Nairobi", 
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Rongai",
  "fee": 12500.00
}
```

**Additional locations with varied pricing:**
```json
POST /api/locations
Content-Type: application/json

{
  "name": "Naivasha",
  "fee": 15000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Karen",
  "fee": 12000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Mombasa",
  "fee": 18000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Mount Kenya",
  "fee": 22000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Kisumu",
  "fee": 14000.00
}
```

**Specialized locations:**
```json
POST /api/locations
Content-Type: application/json

{
  "name": "Nairobi Sports Club",
  "fee": 15000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "University of Nairobi",
  "fee": 18000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "National Museums",
  "fee": 13000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Aberdare National Park",
  "fee": 25000.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "iHub Nairobi",
  "fee": 22000.00
}
```

### Step 2: Populate Camp Categories via API

Based on the PDF specification, create camps for each category using the Camp Management API:

**Endpoint**: `POST /api/camps`

#### A. Young Musicians & Artists Camp (YMAC)

**YMAC camps as specified in PDF:**
```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC Consolata School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "17th-29th Nov",
  "locations": [
    {
      "name": "Kiambu"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC Creative Integrated School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "3rd-20th Nov",
  "locations": [
    {
      "name": "Nairobi"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC - Olerai Rongai School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Dec 1st-12th",
  "locations": [
    {
      "name": "Rongai"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC - Olerai Kiserian School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Dec 1st-12th",
  "locations": [
    {
      "name": "Rongai"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC - Garden Brook School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Dec 1st-12th",
  "locations": [
    {
      "name": "Rongai"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC - Chantilly School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Dec 1st-12th",
  "locations": [
    {
      "name": "Rongai"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "YMAC - White Cottage School",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Dec 1st-12th",
  "locations": [
    {
      "name": "Rongai"
    }
  ]
}
```

#### B. Sports Camps
```json
POST /api/camps
Content-Type: application/json

{
  "name": "Sports Excellence Academy",
  "category": "Sports",
  "campType": "HALF_DAY",
  "dates": "Dec 5-15",
  "locations": [
    {
      "name": "Nairobi Sports Club"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Football Mastery Camp",
  "category": "Sports",
  "campType": "HALF_DAY",
  "dates": "Dec 10-20",
  "locations": [
    {
      "name": "Nairobi Sports Club"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Basketball Skills Development",
  "category": "Sports",
  "campType": "HALF_DAY",
  "dates": "Dec 12-22",
  "locations": [
    {
      "name": "Nairobi Sports Club"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Swimming Champions Program",
  "category": "Sports",
  "campType": "BOOT_CAMP",
  "dates": "Dec 15-25",
  "locations": [
    {
      "name": "Mombasa"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Athletics Performance Camp",
  "category": "Sports",
  "campType": "BOOT_CAMP",
  "dates": "Jan 5-15",
  "locations": [
    {
      "name": "Nairobi Sports Club"
    }
  ]
}
```

#### C. Science & Tech Camps
```json
POST /api/camps
Content-Type: application/json

{
  "name": "Science & Tech Innovation Hub",
  "category": "Science & Tech",
  "campType": "HALF_DAY",
  "dates": "Jan 8-18",
  "locations": [
    {
      "name": "University of Nairobi"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Coding Bootcamp",
  "category": "Science & Tech",
  "campType": "BOOT_CAMP",
  "dates": "Jan 15-25",
  "locations": [
    {
      "name": "iHub Nairobi"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Robotics Workshop",
  "category": "Science & Tech",
  "campType": "HALF_DAY",
  "dates": "Dec 20-30",
  "locations": [
    {
      "name": "University of Nairobi"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "AI & Machine Learning Camp",
  "category": "Science & Tech",
  "campType": "BOOT_CAMP",
  "dates": "Jan 10-20",
  "locations": [
    {
      "name": "iHub Nairobi"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "3D Printing & Design",
  "category": "Science & Tech",
  "campType": "HALF_DAY",
  "dates": "Dec 18-28",
  "locations": [
    {
      "name": "University of Nairobi"
    }
  ]
}
```

#### D. Culture & Heritage Camps
```json
POST /api/camps
Content-Type: application/json

{
  "name": "Cultural Heritage Experience",
  "category": "Culture & Heritage",
  "campType": "HALF_DAY",
  "dates": "Dec 12-22",
  "locations": [
    {
      "name": "National Museums"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Traditional Arts & Crafts",
  "category": "Culture & Heritage",
  "campType": "HALF_DAY",
  "dates": "Dec 15-25",
  "locations": [
    {
      "name": "National Museums"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Kenyan History Explorer",
  "category": "Culture & Heritage",
  "campType": "BOOT_CAMP",
  "dates": "Jan 2-12",
  "locations": [
    {
      "name": "Kisumu"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Language & Literature Camp",
  "category": "Culture & Heritage",
  "campType": "HALF_DAY",
  "dates": "Dec 28-Jan 7",
  "locations": [
    {
      "name": "Nairobi"
    }
  ]
}
```

#### E. Outdoor & Adventure Camps
```json
POST /api/camps
Content-Type: application/json

{
  "name": "Outdoor Adventure Camp",
  "category": "Outdoor & Adventure",
  "campType": "BOOT_CAMP",
  "dates": "Dec 20-30",
  "locations": [
    {
      "name": "Aberdare National Park"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Mount Kenya Expedition",
  "category": "Outdoor & Adventure",
  "campType": "BOOT_CAMP",
  "dates": "Jan 5-15",
  "locations": [
    {
      "name": "Mount Kenya"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Aberdare Wilderness Camp",
  "category": "Outdoor & Adventure",
  "campType": "BOOT_CAMP",
  "dates": "Dec 22-Jan 1",
  "locations": [
    {
      "name": "Aberdare National Park"
    }
  ]
}
```

```json
POST /api/camps
Content-Type: application/json

{
  "name": "Lake Naivasha Adventure",
  "category": "Outdoor & Adventure",
  "campType": "HALF_DAY",
  "dates": "Dec 26-Jan 5",
  "locations": [
    {
      "name": "Naivasha"
    }
  ]
}
```

### Step 3: Populate Activities

Based on the comprehensive activity list from the PDF:

#### YMAC Activities
```sql
-- Get YMAC camp UUIDs for reference
DO $$
DECLARE
    camp_record RECORD;
BEGIN
    FOR camp_record IN 
        SELECT uuid FROM camps WHERE category = 'Young Musicians & Artists Camp (YMAC)'
    LOOP
        INSERT INTO activities (name, description, category, camp_uuid, created_date, created_by) VALUES
        ('Know your Talent Beginners Program', 'Foundation program for discovering artistic talents', 'Foundation', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Photography', 'Learn artistic photography techniques and composition', 'Visual Arts', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Musical Theatre', 'Combine acting, singing, and dancing in theatrical productions', 'Performance', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('String Ensemble', 'Collaborative string instrument performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Marching Band', 'Synchronized musical and visual performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Pop, Rock & RnB', 'Contemporary music genres and performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Ballet', 'Classical dance technique and performance', 'Dance', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Dance', 'Various dance styles and choreography', 'Dance', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Piano Ensemble', 'Group piano performance and accompaniment', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Guitar Ensemble', 'Acoustic and electric guitar performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Jazz Band', 'Jazz music theory and ensemble performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Creative Writing', 'Storytelling, poetry, and literary creation', 'Literature', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Visual Arts', 'Painting, drawing, and mixed media creation', 'Visual Arts', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Deejaying', 'Music mixing and DJ techniques', 'Music Technology', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Voice', 'Vocal training and performance techniques', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Drumline', 'Percussion ensemble and rhythmic performance', 'Music', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Technical Theatre', 'Stage production, lighting, and sound', 'Technical', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Songwriting & Recording', 'Music composition and studio recording', 'Music Technology', camp_record.uuid, CURRENT_TIMESTAMP, 'system');
    END LOOP;
END $$;
```

#### Sports Activities
```sql
DO $$
DECLARE
    camp_record RECORD;
BEGIN
    FOR camp_record IN 
        SELECT uuid FROM camps WHERE category = 'Sports'
    LOOP
        INSERT INTO activities (name, description, category, camp_uuid, created_date, created_by) VALUES
        ('Football', 'Soccer skills development and team play', 'Team Sports', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Basketball', 'Court skills, strategy, and game play', 'Team Sports', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Swimming', 'Water safety and competitive swimming techniques', 'Individual Sports', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Track & Field', 'Running, jumping, and throwing events', 'Athletics', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Volleyball', 'Team coordination and volleyball techniques', 'Team Sports', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Tennis', 'Racquet skills and court strategy', 'Individual Sports', camp_record.uuid, CURRENT_TIMESTAMP, 'system');
    END LOOP;
END $$;
```

#### Science & Tech Activities
```sql
DO $$
DECLARE
    camp_record RECORD;
BEGIN
    FOR camp_record IN 
        SELECT uuid FROM camps WHERE category = 'Science & Tech'
    LOOP
        INSERT INTO activities (name, description, category, camp_uuid, created_date, created_by) VALUES
        ('Coding', 'Programming fundamentals and project development', 'Programming', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Robotics', 'Robot design, building, and programming', 'Engineering', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('3D Printing', 'Digital design and 3D manufacturing', 'Design Technology', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('AI Basics', 'Introduction to artificial intelligence concepts', 'Technology', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Web Development', 'Website creation and online applications', 'Programming', camp_record.uuid, CURRENT_TIMESTAMP, 'system'),
        ('Electronics', 'Circuit design and electronic components', 'Engineering', camp_record.uuid, CURRENT_TIMESTAMP, 'system');
    END LOOP;
END $$;
```

### Step 4: Link Camps to Locations

```sql
-- Link YMAC camps to their specified locations
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.name = 'YMAC Consolata School' AND l.name = 'Kiambu';

INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.name = 'YMAC Creative Integrated School' AND l.name = 'Nairobi';

INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.name LIKE 'YMAC -%' AND l.name = 'Rongai';

-- Link Sports camps to appropriate locations
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.category = 'Sports' AND l.name IN ('Nairobi Sports Club', 'Nairobi', 'Mombasa');

-- Link Science & Tech camps to tech hubs
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.category = 'Science & Tech' AND l.name IN ('iHub Nairobi', 'University of Nairobi', 'Nairobi');

-- Link Culture & Heritage camps to cultural sites
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.category = 'Culture & Heritage' AND l.name IN ('National Museums', 'Nairobi', 'Kisumu');

-- Link Outdoor & Adventure camps to nature locations
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id FROM camps c, locations l 
WHERE c.category = 'Outdoor & Adventure' AND l.name IN ('Aberdare National Park', 'Mount Kenya', 'Naivasha');
```

## Data Validation

### Verify Population Success

```sql
-- Check camp distribution by category
SELECT category, COUNT(*) as camp_count 
FROM camps 
WHERE is_deleted = FALSE 
GROUP BY category;

-- Verify location-camp relationships
SELECT l.name as location, l.fee, COUNT(cl.camp_id) as camp_count
FROM locations l
LEFT JOIN camp_locations cl ON l.id = cl.location_id
GROUP BY l.name, l.fee
ORDER BY l.fee;

-- Check activity distribution
SELECT c.category, COUNT(a.id) as activity_count
FROM camps c
LEFT JOIN activities a ON c.uuid = a.camp_uuid
WHERE c.is_deleted = FALSE AND (a.is_deleted = FALSE OR a.is_deleted IS NULL)
GROUP BY c.category;

-- Verify USSD flow readiness
SELECT 
    'Categories' as metric, 
    COUNT(DISTINCT category) as count 
FROM camps WHERE is_deleted = FALSE
UNION
SELECT 
    'Locations', 
    COUNT(*) 
FROM locations WHERE is_deleted = FALSE
UNION
SELECT 
    'Total Camps', 
    COUNT(*) 
FROM camps WHERE is_deleted = FALSE
UNION
SELECT 
    'Total Activities', 
    COUNT(*) 
FROM activities WHERE is_deleted = FALSE;
```

## USSD Flow Verification

### Test Data Retrieval Queries

```sql
-- Test category selection (Step 1 of USSD flow)
SELECT DISTINCT category FROM camps WHERE is_deleted = FALSE ORDER BY category;

-- Test camp selection with location and pricing (Step 4 of USSD flow)
SELECT 
    c.name,
    l.name as location,
    l.fee,
    c.dates,
    c.camp_type
FROM camps c
JOIN camp_locations cl ON c.id = cl.camp_id
JOIN locations l ON cl.location_id = l.id
WHERE c.category = 'Young Musicians & Artists Camp (YMAC)'
AND c.is_deleted = FALSE;

-- Test activity selection (Step 5 of USSD flow)
SELECT a.name, a.description
FROM activities a
JOIN camps c ON a.camp_uuid = c.uuid
WHERE c.name = 'YMAC Consolata School'
AND a.is_deleted = FALSE
AND a.is_available = TRUE;
```

## Bulk Data Management

### Scripts for Bulk Operations

#### Add Multiple Camps
```sql
-- Template for adding seasonal camps
INSERT INTO camps (name, category, camp_type, dates, created_date, created_by)
SELECT 
    name || ' - ' || season_suffix,
    category,
    camp_type,
    new_dates,
    CURRENT_TIMESTAMP,
    'bulk_import'
FROM (VALUES 
    ('Holiday Special YMAC', 'Young Musicians & Artists Camp (YMAC)', 'HALF_DAY', 'Dec 2024', 'Holiday 2024'),
    ('New Year Sports', 'Sports', 'BOOT_CAMP', 'Jan 2025', 'New Year 2025')
) AS new_camps(name, category, camp_type, new_dates, season_suffix);
```

#### Batch Location Assignment
```sql
-- Assign multiple camps to multiple locations
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id
FROM camps c
CROSS JOIN locations l
WHERE c.category = 'Sports' 
AND l.name IN ('Nairobi Sports Club', 'Mombasa')
AND NOT EXISTS (
    SELECT 1 FROM camp_locations cl2 
    WHERE cl2.camp_id = c.id AND cl2.location_id = l.id
);
```

## Data Maintenance

### Regular Maintenance Tasks

```sql
-- Archive old camps (soft delete)
UPDATE camps 
SET is_deleted = TRUE, deleted_date = CURRENT_TIMESTAMP, deleted_by = 'system'
WHERE dates < '2024-01-01' AND is_deleted = FALSE;

-- Update activity availability
UPDATE activities 
SET is_available = FALSE, updated_date = CURRENT_TIMESTAMP, updated_by = 'system'
WHERE camp_uuid IN (
    SELECT uuid FROM camps WHERE is_deleted = TRUE
);

-- Clean up orphaned activities
UPDATE activities a
SET is_deleted = TRUE, deleted_date = CURRENT_TIMESTAMP, deleted_by = 'system'
WHERE NOT EXISTS (
    SELECT 1 FROM camps c WHERE c.uuid = a.camp_uuid AND c.is_deleted = FALSE
);
```

---

**Next**: [USSD Flow Documentation](./04-USSD_FLOW.md)