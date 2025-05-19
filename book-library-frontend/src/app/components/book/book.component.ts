import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, FormGroup, FormBuilder, Validators, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {BooksService} from '../../services/book.service';
import {Book} from '../../model/book.model';
import {BookRequest} from '../../model/book.request.model';

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
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
  titles: string[] = [];
  categories: string[] = [];
  authors: string[] = [];
  bookForm: FormGroup;
  showBookForm: boolean = false;

  constructor(
    private readonly booksService: BooksService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder
  ) {
    this.bookForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      author: ['', [Validators.required, Validators.minLength(3)]],
      amountPages: [0, [Validators.required, Validators.min(1)]],
      category: ['', Validators.required],
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
    this.loadTitles();
    this.loadCategories();
    this.loadAuthors();
  }

  ngOnInit(): void {
    this.authService.isAdmin.subscribe(isAdmin => {
      this.isAdmin = isAdmin;
    });
  }

  loadTitles() {
    this.booksService.getTitles().subscribe({
      next: (titles) => {
        this.titles = titles;
      },
      error: (err) => this.handleError(err)
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

  searchByTag(type: string, value: string): void {
    this.searchBy = type;
    this.searchTerm = value;
    this.searchBooks();
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

  createBook(): void {
    if (this.bookForm.invalid) {
      Object.keys(this.bookForm.controls).forEach(key => {
        const control = this.bookForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    const newBook: BookRequest = {
      title: this.bookForm.get('title')?.value,
      author: this.bookForm.get('author')?.value,
      amountPages: this.bookForm.get('amountPages')?.value,
      category: this.bookForm.get('category')?.value,
      content: this.bookForm.get('content')?.value
    };

    this.booksService.createBook(newBook).subscribe({
      next: (createdBook) => {
        this.toggleBookForm();
        this.searchBooks();
      },
      error: (err) => this.handleError(err)
    });
  }

  deleteBook(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este libro?')) {
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

  toggleBookForm(): void {
    this.showBookForm = !this.showBookForm;
    if (!this.showBookForm) {
      this.bookForm.reset();
    }
  }
}
