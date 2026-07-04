import { Component } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

@Component({
  selector: 'app-sobre',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './sobre.html',
  styleUrl: './sobre.css',
})
export class Sobre {}
