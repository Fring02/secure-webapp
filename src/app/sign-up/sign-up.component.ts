import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {
  name: string = '';
  email: string = '';
  username: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(private router: Router) {}

  onSubmit(formData: any) {
    console.log('Login Data:', formData);
    // After validating login
    this.router.navigate(['/login']);  // Navigate to the upload component
  }

  navigateToLogin() {
    console.log('enterd navigateToLogin method.');
    this.router.navigate(['/login']);
  }
}
