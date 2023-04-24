import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddCategoriesComponent } from './add-categories/add-categories.component';
import { ListCategoriesComponent } from './list-categories/list-categories.component';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { DetailCategoriesComponent } from './detail-categories/detail-categories.component';
import { EditCategoriesComponent } from './edit-categories/edit-categories.component';


const routes: Routes = [
  {
    path: 'list',
    component: ListCategoriesComponent,
    data: { animation: 'feather' }
  }
];


@NgModule({
  declarations: [
    ListCategoriesComponent,
    AddCategoriesComponent,
    EditCategoriesComponent,
    DetailCategoriesComponent,
  ],
  imports: [
    CommonModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    CorePipesModule,
    ReactiveFormsModule,
    FormsModule,
    NgSelectModule,
    ContentHeaderModule,
    RouterModule.forChild(routes),
    TranslateModule

  ], exports:[
    DetailCategoriesComponent
  ]
})
export class CategoriesModule { }
