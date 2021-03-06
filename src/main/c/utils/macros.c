/* [{
Copyright 2007, 2008 Nicolas Carranza <nicarran at gmail.com>

This file is part of jpen.

jpen is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

jpen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with jpen.  If not, see <http://www.gnu.org/licenses/>.
}] */
#include "macros.h"

/**
s1 must be: NULL, or have been obtained with malloc.
*/
char *mf_concat(char *s1, char *s2){
	// total length of s1 + s2
  int size=0;
  if(s2!=NULL)
	 size+=strlen(s2);
  if(s1!=NULL)
	 size+=strlen(s1);
 	
 	// allocate a new char string:
  char *newS1=malloc((size+1)*sizeof(char));
  newS1[0]='\0';
  
  // copy s1 and s2 contents to the new char string:
  if(s1!=NULL){
	 strcpy(newS1, s1);
	 free(s1);
  }
  if(s2!=NULL)
	 strcat(newS1, s2);
  return newS1;
}
