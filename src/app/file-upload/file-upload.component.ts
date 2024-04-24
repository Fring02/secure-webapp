import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.css'
})
export class FileUploadComponent {
  filesToUpload: File[] = [];
  uploadedFiles: File[] = [];

  constructor(private router: Router) {}

  onFileSelected(event: any) {
    this.filesToUpload = Array.from(event.target.files);
  }

  onUpload() {
    // Simulate upload logic
    this.uploadedFiles = this.filesToUpload;  // Pretend we upload files
    this.router.navigate(['/files'], { state: { files: this.uploadedFiles } });
  }
}
