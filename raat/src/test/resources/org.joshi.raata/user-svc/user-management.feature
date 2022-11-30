Feature: User Management
  This features validates user management service

  Scenario: Create a new user
    Given The library management system is running
    When Admin user creates a user with username 'testUser', name 'TestUser' and password 'testPassword'
    And Admin fetches the user with username 'testUser'
    Then The fetched user should have username the following
      | Username testUser     |
      | Name TestUser         |
      | Password testPassword |

