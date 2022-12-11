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
      | username    | testUser |
      | displayName | TestUser |
      | password    | testUser |

  @ValidateBorrow
  Scenario: User borrows book from the library
    Given I am logged in as the 'testUser' user
    When I reserve a book with following details
      | reservationId | 1234     |
      | bookName      | TestBook |
      | quantity      | 2        |
      | numberOfDays  | 7        |
    And I retrieve the reservation with id '1234'
    Then The fetched reservation should have following details
      | reservationId | 1234     |
      | bookName      | TestBook |
      | quantity      | 2        |
      | numberOfDays  | 7        |

  Scenario: User borrows book from the library when not enough books
    Given I am logged in as the 'testUser' user
    When I reserve a book with following details
      | reservationId | 1234     |
      | bookName      | TestBook |
      | quantity      | 7        |
      | numberOfDays  | 7        |
    Then I get an 'Bad Request' error

  Scenario: User borrows book from the library that does not exist
    Given I am logged in as the 'testUser' user
    When I reserve a book with following details
      | reservationId | 1234      |
      | bookName      | TestBook5 |
      | quantity      | 7         |
      | numberOfDays  | 7         |
    Then I get an 'Not Found' error
