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

**YMAC School Locations (Primary locations for the camp):**
```json
POST /api/locations
Content-Type: application/json

{
  "name": "Consolata School Kiambu",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Creative Integrated School Nairobi", 
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Olerai Rongai School",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Olerai Kiserian School",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Garden Brook School",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "Chantilly School",
  "fee": 12500.00
}
```

```json
POST /api/locations
Content-Type: application/json

{
  "name": "White Cottage School",
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

**YMAC - Single camp offered at multiple school locations:**
```json
POST /api/camps
Content-Type: application/json

{
  "name": "Young Musicians & Artists Camp (YMAC)",
  "category": "Young Musicians & Artists Camp (YMAC)",
  "campType": "HALF_DAY",
  "dates": "Nov-Dec 2024",
  "locations": [
    {
      "name": "Consolata School Kiambu"
    },
    {
      "name": "Creative Integrated School Nairobi"
    },
    {
      "name": "Olerai Rongai School"
    },
    {
      "name": "Olerai Kiserian School"
    },
    {
      "name": "Garden Brook School"
    },
    {
      "name": "Chantilly School"
    },
    {
      "name": "White Cottage School"
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
```json
-- Note: Activities are created via API for the single YMAC camp
POST /api/activities
Content-Type: application/json

[
  {
    "name": "Know your Talent Beginners Program",
    "description": "Foundation program for discovering artistic talents",
    "category": "Foundation",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Photography",
    "description": "Learn artistic photography techniques and composition",
    "category": "Visual Arts",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Musical Theatre",
    "description": "Combine acting, singing, and dancing in theatrical productions",
    "category": "Performance",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "String Ensemble",
    "description": "Collaborative string instrument performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Marching Band",
    "description": "Synchronized musical and visual performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Pop, Rock & RnB",
    "description": "Contemporary music genres and performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Ballet",
    "description": "Classical dance technique and performance",
    "category": "Dance",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Dance",
    "description": "Various dance styles and choreography",
    "category": "Dance",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Piano Ensemble",
    "description": "Group piano performance and accompaniment",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Guitar Ensemble",
    "description": "Acoustic and electric guitar performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Jazz Band",
    "description": "Jazz music theory and ensemble performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Creative Writing",
    "description": "Storytelling, poetry, and literary creation",
    "category": "Literature",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Visual Arts",
    "description": "Painting, drawing, and mixed media creation",
    "category": "Visual Arts",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Deejaying",
    "description": "Music mixing and DJ techniques",
    "category": "Music Technology",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Voice",
    "description": "Vocal training and performance techniques",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Drumline",
    "description": "Percussion ensemble and rhythmic performance",
    "category": "Music",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Technical Theatre",
    "description": "Stage production, lighting, and sound",
    "category": "Technical",
    "campUuid": "<YMAC_CAMP_UUID>"
  },
  {
    "name": "Songwriting & Recording",
    "description": "Music composition and studio recording",
    "category": "Music Technology",
    "campUuid": "<YMAC_CAMP_UUID>"
  }
]
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

**Note:** Camp-location relationships are automatically established through the API when creating camps with location arrays.

```sql
-- Verify YMAC camp is linked to all school locations
SELECT c.name as camp_name, l.name as location_name, l.fee
FROM camps c
JOIN camp_locations cl ON c.id = cl.camp_id
JOIN locations l ON cl.location_id = l.id
WHERE c.name = 'Young Musicians & Artists Camp (YMAC)';

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
WHERE c.name = 'Young Musicians & Artists Camp (YMAC)'
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