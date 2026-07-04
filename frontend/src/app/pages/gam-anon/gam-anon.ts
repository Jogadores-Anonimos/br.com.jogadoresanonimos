import { Component } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

@Component({
  selector: 'app-gam-anon',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './gam-anon.html',
  styleUrl: './gam-anon.css'
})
export class GamAnon {
}
