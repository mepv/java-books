import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-callback',
  imports: [],
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css'
})
export class CallbackComponent implements OnInit {
  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthService
  ) {
  }

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');

    if (code && state) {
      this.authService.handleCallback(code, state).subscribe(
        (response) => {
          if (response.error) {
            console.error('Auth error:', response.error);
            console.error('Auth error:', response.error_description);
            this.router.navigate(['/login'], {
              queryParams: {error: 'authentication_failed'}
            }).then(() => {
            });
          }
        });
    } else {
      this.router.navigate(['/login']).then(() => {
      });
    }
  }
}
