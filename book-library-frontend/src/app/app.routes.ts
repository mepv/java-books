import {Routes} from '@angular/router';
import {CallbackComponent} from './components/callback/callback.component';
import {authGuard} from './auth.guard';
import {BookComponent} from './components/book/book.component';
import {LandingComponent} from './components/landing/landing.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent
  },
  {
    path: 'books',
    component: BookComponent,
    canActivate: [authGuard]
  },
  {
    path: 'oauth/callback',
    component: CallbackComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
