import { CoreCommonModule } from '@core/common.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ErrorHandler, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { HomepageModule } from './homepage/homepage.module';
import { HomepageComponent } from './homepage/homepage/homepage.component';


// routing
const routes: Routes = [
  {
    path: 'list',
    loadChildren: () => import('./homepage/homepage.module').then(m => m.HomepageModule)
  },
];

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    HomepageModule,
    RouterModule.forChild(routes),
    NgbModule,
    CoreCommonModule,
  ]
})
export class DashboardModule { }
