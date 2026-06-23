import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-barra-deslizante',
  standalone: true,
  imports: [],
  templateUrl: './barra-deslizante.html',
  styleUrls: ['./barra-deslizante.css'],
  encapsulation: ViewEncapsulation.None
})
export class BarraDeslizante {
  @Input() messages: string[] = [];
  @Input() speed: number = 30;
  @Input() color: string = '#C9A84C';
  @Input() separator: string = '•';
  paused = false;
}
