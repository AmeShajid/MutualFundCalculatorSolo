import { Component, HostListener } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  // Track if we're on the home page to show section scroll links
  isHomePage = true;
  // Track which section is active for scroll highlighting
  activeSection = 'hero';

  constructor(private router: Router) {
    // Listen for route changes to toggle home page mode
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        this.isHomePage = e.urlAfterRedirects === '/' || e.urlAfterRedirects === '';
      });
  }

  // Scroll to a section on the home page
  scrollTo(sectionId: string): void {
    const el = document.getElementById(sectionId);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  }

  // Update active section based on scroll position
  @HostListener('window:scroll')
  onScroll(): void {
    if (!this.isHomePage) return;

    const sections = ['hero', 'features', 'formula', 'tech'];
    for (let i = sections.length - 1; i >= 0; i--) {
      const el = document.getElementById(sections[i]);
      if (el && window.scrollY >= el.offsetTop - 120) {
        this.activeSection = sections[i];
        break;
      }
    }
  }
}
