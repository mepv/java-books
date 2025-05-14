import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly AUTH_SERVER = 'http://localhost:9000';
  private readonly CLIENT_ID = 'c19172df-023f-405e-b422-f05aef5f1e12';
  private readonly REDIRECT_URI = 'http://localhost:4200/oauth/callback';
  private processingCallback = false;

  private readonly _isAdmin = new BehaviorSubject<boolean>(false);
  public isAdmin = this._isAdmin.asObservable();
  private readonly _token = new BehaviorSubject<string | null>(null);
  public token = this._token.asObservable();

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    const storedToken = localStorage.getItem('access_token');
    if (storedToken) {
      this._token.next(storedToken);
    }
  }

  async loginUser() {
    await this.login('openid email book:read')
  }

  async loginAdmin() {
    await this.login('openid email book:read book:write')
  }

  private async login(scope: string) {
    const authorizationEndpoint = `${this.AUTH_SERVER}/oauth2/authorize`;
    const codeVerifier = this.generateCodeVerifier();
    sessionStorage.setItem('code_verifier', codeVerifier);
    const codeChallenge = await this.generateCodeChallenge(codeVerifier);
    const state = this.generateRandomState();
    sessionStorage.setItem('auth_state', state);

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: this.CLIENT_ID,
      redirect_uri: this.REDIRECT_URI,
      scope: scope,
      code_challenge: codeChallenge,
      code_challenge_method: 'S256',
      state: state
    });

    window.location.href = `${authorizationEndpoint}?${params.toString()}`;
  }

  logout() {
    localStorage.removeItem('access_token');
    this._token.next(null);

    const logoutParams = new URLSearchParams({
      client_id: this.CLIENT_ID,
      redirect_uri: 'http://localhost:4200/login'
    });

    window.location.href = `${this.AUTH_SERVER}/logout?${logoutParams.toString()}`;
    this.router.navigate(['/']).then(() => {});
  }

  handleCallback(code: string, state: string): Observable<any> {
    if (this.processingCallback) {
      return of(null);
    }
    this.processingCallback = true;

    try {
      const storedState = sessionStorage.getItem('auth_state');
      if (state !== storedState) {
        this.processingCallback = false;
        return of({ error: 'Invalid state parameter' });
      }
      return this.exchangeCodeForToken(code, state);
    } catch (error) {
      this.processingCallback = false;
      return of({ error: error });
    }
  }

  private exchangeCodeForToken(code: string, state: string): Observable<any> {
    const tokenEndpoint = `${this.AUTH_SERVER}/oauth2/token`;
    const codeVerifier = sessionStorage.getItem('code_verifier');

    if (!codeVerifier) {
      this.processingCallback = false;
      throw new Error('Code verifier not found');
    }

    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      code: code,
      redirect_uri: this.REDIRECT_URI,
      client_id: this.CLIENT_ID,
      code_verifier: codeVerifier
    });

    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    return this.http.post(tokenEndpoint, body.toString(), { headers })
      .pipe(
        tap((response: any) => {
          sessionStorage.removeItem('code_verifier');
          sessionStorage.removeItem('auth_state');

          localStorage.setItem('access_token', response.access_token);
          this._token.next(response.access_token);

          this.parseToken(response.access_token);
          this.processingCallback = false;
          this.router.navigate(['/books']).then(()=> {});
        }),
        catchError(error => {
          this.processingCallback = false;
          console.error('Token exchange error:', error);
          return of({ error: 'Failed to exchange code for token' });
        })
      );
  }

  getToken(): string | null {
    return this._token.value;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private parseToken(token: string): void {
    try {
      const payloadBase64 = token.split('.')[1];
      const payload = JSON.parse(atob(payloadBase64));

      let scopes: string[] = [];
      if (payload.scope) {
        if (typeof payload.scope === 'string') {
          scopes = payload.scope.split(' ');
        } else if (Array.isArray(payload.scope)) {
          scopes = payload.scope;
        } else {
          console.warn('Unexpected scope format:', payload.scope);
        }
      }
      const hasWriteScope = scopes.includes('book:write');

      this._isAdmin.next(hasWriteScope);
      localStorage.setItem('isAdmin', hasWriteScope.toString());
    } catch (err) {
      console.error('Error parsing token', err);
      this._isAdmin.next(false);
    }
  }

  private async generateCodeChallenge(verifier: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);

    const hashBuffer = await window.crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashBase64 = btoa(String.fromCharCode(...hashArray));

    // Convert base64 to base64url (replace +, / and remove =)
    return hashBase64
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=/g, '');
  }

  private generateRandomState(): string {
    const array = new Uint8Array(24);
    window.crypto.getRandomValues(array);
    return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('');
  }

  private generateCodeVerifier(): string {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let verifier = '';
    const randomValues = new Uint8Array(128);
    window.crypto.getRandomValues(randomValues);

    for (let i = 0; i < 128; i++) {
      verifier += charset.charAt(randomValues[i] % charset.length);
    }
    return verifier;
  }
}
