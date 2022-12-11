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

