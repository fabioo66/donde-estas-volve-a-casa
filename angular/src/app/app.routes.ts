import { Routes } from '@angular/router';
import { Home } from './home/home';
import { MascotaFormComponent } from './components/mascota-form/mascota-form.component';
import { MascotaEditComponent } from './components/mascota-edit/mascota-edit.component';
import { MascotaListComponent } from './components/mascota-list/mascota-list.component';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'mascotas',
    component: MascotaListComponent
  },
  {
    path: 'mascota/nuevo',
    component: MascotaFormComponent
  },
  {
    path: 'mascota/:id/editar',
    component: MascotaEditComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
