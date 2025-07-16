import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  timeRanges = [
    { value: 'day', viewValue: 'Today' },
    { value: 'week', viewValue: 'This Week' },
    { value: 'month', viewValue: 'This Month' },
    { value: 'year', viewValue: 'This Year' },
  ];
  locations = [
    { value: 'all', viewValue: 'All Locations' },
    { value: 'new-york', viewValue: 'New York' },
    { value: 'london', viewValue: 'London' },
    { value: 'tokyo', viewValue: 'Tokyo' },
  ];

  selectedTimeRange = signal(this.timeRanges[0].value);
  selectedLocation = signal(this.locations[0].value);
}
