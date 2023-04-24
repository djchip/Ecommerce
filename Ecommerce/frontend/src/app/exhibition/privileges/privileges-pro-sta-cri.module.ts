import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { TreeModule } from '@circlon/angular-tree-component';
import { PrivilegesProStaCriComponent } from './privileges-pro-sta-cri/privileges-pro-sta-cri.component';

const routes: Routes = [

  {
    path: 'list',
    component: PrivilegesProStaCriComponent,
    data: { animation: 'feather' }
  },

];

@NgModule({
  declarations: [
    PrivilegesProStaCriComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule,
    TreeModule
  ]
})
export class PrivilegesProStaCriModule { }
