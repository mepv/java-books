import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  constructor(private readonly authService: AuthService) {}

  loginUser() {
    this.authService.loginUser()
      .then(r => console.log('Login successful', r));
  }

  loginAdmin() {
    this.authService.loginAdmin()
      .then(r => console.log('Login successful', r));
  }
}
