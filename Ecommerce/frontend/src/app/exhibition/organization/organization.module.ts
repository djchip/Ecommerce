import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListComponent } from './list/list.component';
import { RouterModule, Routes } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { AddComponent } from './add/add.component';
import { EditComponent } from './edit/edit.component';
import { DetailComponent } from './detail/detail.component';

// routing
const routes: Routes = [
  {
    path: 'list',
    component: ListComponent,
  },
];

@NgModule({
  declarations: [
    ListComponent,
    AddComponent,
    EditComponent,
    DetailComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    TranslateModule
  ],
  exports:[
    DetailComponent,
  ]
})
export class OrganizationModule { }
