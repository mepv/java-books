import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Book} from '../model/book.model';
import {BookRequest} from '../model/book.request.model';

@Injectable({
  providedIn: 'root'
})
export class BooksService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/books';

  constructor(private readonly http: HttpClient) {}

  getTitles(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/titles`);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }

  getAuthors(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/authors`);
  }

  getBookByTitle(title: string): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/title/${title}`);
  }

  getBooksByAuthor(author: string): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiUrl}/author/${author}`);
  }

  createBook(book: BookRequest): Observable<BookRequest> {
    return this.http.post<BookRequest>(`${this.apiUrl}`, book);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
