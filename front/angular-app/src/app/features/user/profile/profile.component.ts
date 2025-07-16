import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { User } from '../user.model'
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './profile.component.html',
  styleUrls: []
})
export class ProfileComponent implements OnInit {
  user: User | undefined;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getUser().subscribe(user => {
      this.user = user;
    });
  }
}
