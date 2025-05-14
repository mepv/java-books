import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {BooksService} from '../../services/book.service';
import {Book} from '../../model/book.model';

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  isAdmin = false;
  searchTerm = '';
  searchBy = 'title'; // Default search by title
  books: Book[] = [];
  searched = false;
  selectedBook: Book | null = null;
  singleBookResult: Book | null = null;
  categories: string[] = [];
  authors: string[] = [];

  constructor(
    private readonly booksService: BooksService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    this.loadCategories();
    this.loadAuthors();
  }

  ngOnInit(): void {
    this.authService.isAdmin.subscribe(isAdmin => {
      this.isAdmin = isAdmin;
    });
  }

  loadCategories() {
    this.booksService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => this.handleError(err)
    });
  }

  loadAuthors() {
    this.booksService.getAuthors().subscribe({
      next: (authors) => {
        this.authors = authors;
      },
      error: (err) => this.handleError(err)
    });
  }

  searchBooks() {
    if (!this.searchTerm.trim()) return;
    this.searched = true;
    this.singleBookResult = null;

    if (this.searchBy === 'title') {
      this.searchByTitle();
    } else if (this.searchBy === 'author') {
      this.searchByAuthor();
    }
  }

  searchByTitle() {
    this.booksService.getBookByTitle(this.searchTerm).subscribe({
      next: (result) => {
        if (Array.isArray(result)) {
          this.books = result;
          this.singleBookResult = null;
        } else if (result) {
          this.books = [];
          this.singleBookResult = result;
          this.selectedBook = this.singleBookResult;
        } else {
          this.books = [];
          this.singleBookResult = null;
        }
      },
      error: (err) => this.handleError(err)
    });
  }

  searchByAuthor() {
    this.booksService.getBooksByAuthor(this.searchTerm).subscribe({
      next: (result) => {
        this.books = result;
        this.selectedBook = null;
      },
      error: (err) => this.handleError(err)
    });
  }

  selectBook(book: Book) {
    this.selectedBook = book;
  }

  closeDetails() {
    this.selectedBook = null;
  }

  createBook(book: Book) {
    this.booksService.createBook(book).subscribe({
      next: () => {
        this.searchBooks();
      },
      error: (err) => this.handleError(err)
    });
  }

  deleteBook(id: number) {
    this.booksService.deleteBook(id).subscribe({
      next: () => {
        this.searchBooks();
        if (this.selectedBook?.id === id) {
          this.selectedBook = null;
        }
      },
      error: (err: any) => this.handleError(err)
    });
  }

  handleError(err: any) {
    console.error('Error', err);
    if (err.status === 401) {
      // todo: complete
    }
    if (err.status === 403) {
      // todo: complete
    }
  }

  logout() {
    this.authService.logout();
  }
}
