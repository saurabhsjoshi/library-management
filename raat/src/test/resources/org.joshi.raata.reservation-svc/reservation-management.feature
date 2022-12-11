Feature: Reservation management
  This feature validates borrow/return logic of the system

  Background:
    Given The library management system is running
    When I am logged in as the 'admin' user
    Then I create a book with following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |
    And Admin user creates a user with following details
      | username    | testUser     |
      | displayName | TestUser     |
      | password    | testPassword |

  Scenario: User borrows book from the library
    Given I am logged in as the 'testUser' user
    When I reserve a book with following details
      | bookName | TestBook |
      | quantity | 2        |
    And I retrieve the book with book name 'TestBook'
    Then The fetched book should have the following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 3          |
