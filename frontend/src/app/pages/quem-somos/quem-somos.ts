import { Component, ViewEncapsulation } from '@angular/core';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-quem-somos',
  standalone: true,
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './quem-somos.html',
  styleUrl: './quem-somos.css',
  encapsulation: ViewEncapsulation.None
})
export class QuemSomos {}
