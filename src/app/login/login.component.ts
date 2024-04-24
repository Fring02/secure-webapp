import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private router: Router) {}

  onSubmit(formData: any) {
    console.log('Login Data:', formData);
    // After validating login
    this.router.navigate(['/upload']);  // Navigate to the upload component
  }

  navigateToSignup() {
    this.router.navigate(['/signup']);
  }

}
