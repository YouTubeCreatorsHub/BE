##프로젝트 구조

erDiagram
    USERS ||--o{ CREATOR_PROFILES : has
    USERS ||--o{ SOCIAL_ACCOUNTS : manages
    USERS ||--o{ CONTENTS : creates
    USERS ||--o{ NOTIFICATIONS : receives
    
    CREATOR_PROFILES ||--o{ ANALYTICS : tracks
    CREATOR_PROFILES ||--o{ EARNINGS : records
    CREATOR_PROFILES }o--o{ COLLABORATIONS : participates
    
    CONTENTS ||--o{ COMMENTS : has
    CONTENTS ||--o{ ANALYTICS : generates
    CONTENTS ||--o{ RESOURCES : uses
    
    RESOURCES ||--o{ RESOURCE_CATEGORIES : belongs_to
    
    COLLABORATIONS ||--o{ MESSAGES : contains
    
    ANALYTICS ||--o{ ANALYTICS_METRICS : contains

    USERS {
        UUID id PK
        string email
        string password_hash
        string name
        string phone
        datetime created_at
        datetime updated_at
        boolean is_active
        string role
    }

    CREATOR_PROFILES {
        UUID id PK
        UUID user_id FK
        string channel_name
        string description
        string profile_image
        string category
        jsonb social_links
        datetime created_at
        datetime updated_at
    }

    SOCIAL_ACCOUNTS {
        UUID id PK
        UUID user_id FK
        string platform
        string account_id
        jsonb credentials
        boolean is_connected
        datetime connected_at
    }

    CONTENTS {
        UUID id PK
        UUID creator_id FK
        string title
        string description
        string content_type
        string status
        jsonb metadata
        datetime published_at
        datetime created_at
        datetime updated_at
    }

    ANALYTICS {
        UUID id PK
        UUID content_id FK
        UUID creator_id FK
        datetime date
        jsonb metrics
        datetime created_at
    }

    ANALYTICS_METRICS {
        UUID id PK
        UUID analytics_id FK
        string metric_name
        float metric_value
        string metric_type
        datetime recorded_at
    }

    RESOURCES {
        UUID id PK
        string name
        string type
        string url
        string license_type
        jsonb metadata
        datetime created_at
        datetime updated_at
    }

    RESOURCE_CATEGORIES {
        UUID id PK
        string name
        string description
        datetime created_at
    }

    COLLABORATIONS {
        UUID id PK
        UUID creator1_id FK
        UUID creator2_id FK
        string status
        datetime start_date
        datetime end_date
        jsonb terms
    }

    MESSAGES {
        UUID id PK
        UUID collaboration_id FK
        UUID sender_id FK
        text content
        datetime sent_at
        boolean is_read
    }

    NOTIFICATIONS {
        UUID id PK
        UUID user_id FK
        string type
        string title
        text content
        boolean is_read
        datetime created_at
    }

    EARNINGS {
        UUID id PK
        UUID creator_id FK
        decimal amount
        string currency
        string source
        string status
        datetime earned_at
        datetime paid_at
    }
