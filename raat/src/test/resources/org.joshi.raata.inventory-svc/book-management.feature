Feature: Books Management
  This features validates the book management API

  Scenario: Add book to inventory
    Given The library management system is running
    And I am logged in as the 'admin' user
    When I create a book with following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |
    And I retrieve the book with book name 'TestBook'
    Then The fetched book should have the following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |

  Scenario: Add book to inventory as a non-admin user
    Given The library management system is running
    And I am logged in as the 'testUser' user
    When I create a book with following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |
    Then I get an 'Unauthorized' error

  Scenario: Add book to inventory
    Given The library management system is running
    And I am logged in as the 'admin' user
    When I create a book with following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |
    And I update the quantity of a book with name 'TestBook' as follows
      | quantity | 1 |
    And I retrieve the book with book name 'TestBook'
    Then The fetched book should have the following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 6          |

  Scenario: Delete book from inventory
    Given The library management system is running
    And I am logged in as the 'admin' user
    When I create a book with following information
      | bookName    | TestBook   |
      | author      | TestAuthor |
      | publishYear | 1994       |
      | quantity    | 5          |
    And I delete a book with name 'TestBook'
    And I retrieve the book with book name 'TestBook'
    Then I get an 'Not Found' error