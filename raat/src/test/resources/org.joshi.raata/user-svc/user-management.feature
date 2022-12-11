Feature: User Management
  This features validates user management service

  Scenario: Create a new user
    Given The library management system is running
    When Admin user creates a user with following details
      | username    | testUser     |
      | displayName | TestUser     |
      | password    | testPassword |
    And Admin fetches the user with username 'testUser'
    Then The fetched user should have username the following
      | username    | testUser     |
      | displayName | TestUser     |
      | password    | testPassword |

