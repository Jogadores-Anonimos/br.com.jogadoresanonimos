import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

@Component({
  selector: 'app-boletim-informativo',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './boletim-informativo.html',
  styleUrl: './boletim-informativo.css',
})
export class BoletimInformativo {}
